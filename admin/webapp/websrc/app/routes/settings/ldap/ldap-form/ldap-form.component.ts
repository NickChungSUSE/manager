import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import {
  ErrorResponse,
  GroupMappedRole,
  LDAP,
  ServerGetResponse,
  ServerPatchBody,
} from '@common/types';
import { SettingsService } from '@services/settings.service';
import { finalize } from 'rxjs/operators';
import { TestConnectionDialogComponent } from '../test-connection-dialog/test-connection-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { UtilsService } from '@common/utils/app.utils';
import { NotificationService } from '@services/notification.service';
import { Observable } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { AuthUtilsService } from '@common/utils/auth.utils';

@Component({
  selector: 'app-ldap-form',
  templateUrl: './ldap-form.component.html',
  styleUrls: ['./ldap-form.component.scss'],
})
export class LdapFormComponent implements OnInit, OnChanges {
  @Input() ldapData!: { server: ServerGetResponse; domains: string[] };
  @Output() refresh = new EventEmitter();

  isWriteLdapAuthorized: boolean = false;
  isCreated = true;
  submittingForm = false;
  groupMappedRoles: GroupMappedRole[] = [];
  serverName = 'ldap1';
  passwordVisible = false;
  ldapForm = new UntypedFormGroup({
    directory: new UntypedFormControl(),
    hostname: new UntypedFormControl(null, Validators.required),
    port: new UntypedFormControl(),
    ssl: new UntypedFormControl(),
    bind_dn: new UntypedFormControl(),
    bind_password: new UntypedFormControl(),
    base_dn: new UntypedFormControl(null, Validators.required),
    username_attr: new UntypedFormControl(),
    group_member_attr: new UntypedFormControl(),
    default_role: new UntypedFormControl(''),
    enable: new UntypedFormControl(false),
  });

  constructor(
    private authUtilsService: AuthUtilsService,
    private settingsService: SettingsService,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private utils: UtilsService,
    private tr: TranslateService
  ) {}

  ngOnInit(): void {
    this.isWriteLdapAuthorized =
      this.authUtilsService.getDisplayFlag('write_auth_server');
    if (!this.isWriteLdapAuthorized) {
      this.ldapForm.disable();
    }
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.ldapData) {
      this.ldapForm.reset();
      this.initForm();
    }
  }

  initForm(): void {
    const ldap = this.ldapData.server.servers.find(
      ({ server_type }) => server_type === 'ldap'
    );
    if (ldap && ldap.ldap) {
      this.serverName = ldap.server_name;
      this.groupMappedRoles = ldap.ldap.group_mapped_roles;
      Object.keys(ldap.ldap).forEach((key: string) => {
        if (this.ldapForm.controls[key]) {
          this.ldapForm.controls[key].setValue(
            ldap.ldap ? ldap.ldap[key] : null
          );
        }
      });
      this.ldapForm.get('bind_password')?.markAsPristine();
    } else {
      this.isCreated = false;
    }
  }

  submitForm(): void {
    if (!this.ldapForm.valid) {
      return;
    }
    const ldap: LDAP = {
      group_mapped_roles: this.groupMappedRoles,
      ...this.ldapForm.value,
    };
    if (!this.ldapForm.get('bind_password')?.dirty) {
      ldap.bind_password = null as any;
    }
    const config: ServerPatchBody = { config: { name: this.serverName, ldap } };
    this.submittingForm = true;
    let submission: Observable<unknown>;
    if (!this.isCreated) {
      submission = this.settingsService.postServer(config).pipe(
        finalize(() => {
          this.submittingForm = false;
          this.isCreated = true;
        })
      );
    } else {
      submission = this.settingsService.patchServer(config).pipe(
        finalize(() => {
          this.submittingForm = false;
        })
      );
    }
    submission.subscribe({
      complete: () => {
        this.notificationService.open(this.tr.instant('ldap.SERVER_SAVED'));
        this.refresh.emit();
      },
      error: ({ error }: { error: ErrorResponse }) => {
        this.notificationService.open(
          this.utils.getAlertifyMsg(
            error,
            this.tr.instant('ldap.SERVER_SAVE_FAILED'),
            false
          )
        );
      },
    });
  }

  openDialog(): void {
    const ldap: LDAP = {
      group_mapped_roles: this.groupMappedRoles,
      ...this.ldapForm.value,
    };
    this.dialog.open(TestConnectionDialogComponent, {
      width: '60%',
      data: {
        ldap,
        name: this.serverName,
      },
    });
  }

  updateGroupMappedRoles(newGroupMappedRoles: GroupMappedRole[]): void {
    this.groupMappedRoles = newGroupMappedRoles;
  }
}
