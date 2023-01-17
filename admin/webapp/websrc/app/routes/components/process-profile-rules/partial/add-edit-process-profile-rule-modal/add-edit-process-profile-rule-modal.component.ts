import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UntypedFormGroup, UntypedFormControl, Validators } from '@angular/forms';
import { UtilsService } from '@common/utils/app.utils';
import { ProcessProfileRulesService } from '@services/process-profile-rules.service';
import { GroupsService } from '@services/groups.service';
import { GlobalConstant } from '@common/constants/global.constant';
import { TranslateService } from '@ngx-translate/core';
import { NotificationService } from '@services/notification.service';

@Component({
  selector: 'app-add-edit-process-profile-rule-modal',
  templateUrl: './add-edit-process-profile-rule-modal.component.html',
  styleUrls: ['./add-edit-process-profile-rule-modal.component.scss'],
})
export class AddEditProcessProfileRuleModalComponent implements OnInit {
  public processProfileRule;
  public readonly type: string = '';
  public groupOptions: Array<string>;
  public isAllowed: Boolean = true;
  public processProfileRuleForm: UntypedFormGroup;

  constructor(
    public dialogRef: MatDialogRef<AddEditProcessProfileRuleModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private processProfileRulesService: ProcessProfileRulesService,
    private groupsService: GroupsService,
    private translate: TranslateService,
    private utils: UtilsService,
    private notificationService: NotificationService
  ) {
    this.type = this.data.type;

  }

  ngOnInit(): void {
    this.processProfileRuleForm = new UntypedFormGroup({
      group: new UntypedFormControl(
        { value: '', disabled: this.type === GlobalConstant.MODAL_OP.EDIT || this.data.source === GlobalConstant.NAV_SOURCE.GROUP },
        [Validators.required]
      ),
      name: new UntypedFormControl('', Validators.required),
      path: new UntypedFormControl(''),
    });
    this.initializeVM();
    this.getGroupOptions();
  }

  onCancel() {
    this.dialogRef.close();
  }

  getGroupOptions() {
    this.groupsService.getGroupList(
      this.data.source === GlobalConstant.NAV_SOURCE.FED_POLICY ?
      GlobalConstant.SCOPE.FED :
      GlobalConstant.SCOPE.LOCAL
    ).subscribe(
      response => {
        this.groupOptions = response['groups']
          .map(group => group.name)
          .filter(group => group !== GlobalConstant.EXTERNAL);
      },
      err => {
        console.warn(err);
      }
    );
  }

  initializeVM() {
    if (this.type === GlobalConstant.MODAL_OP.ADD) {
      this.processProfileRuleForm.reset();
    } else {
      Object.keys(this.data.oldData).forEach((key: string) => {
        if (this.processProfileRuleForm.controls[key]) {
          this.processProfileRuleForm.controls[key].setValue(
            this.data.oldData ? this.data.oldData[key] : null
          );
        }
      });
      this.isAllowed =
        this.data.oldData.action ===
        GlobalConstant.PROCESS_PROFILE_RULE.ACTION.ALLOW;
    }
    if (!this.processProfileRuleForm.controls.group.value) {
      this.processProfileRuleForm.controls.group.setValue(this.data.groupName || "");
    }
  }

  addEditProcessProfileRule() {
    let typeText =
      this.type === GlobalConstant.MODAL_OP.ADD
        ? ['added', 'add']
        : ['updated', 'update'];
    let newData = {
      action: this.isAllowed
        ? GlobalConstant.PROCESS_PROFILE_RULE.ACTION.ALLOW
        : GlobalConstant.PROCESS_PROFILE_RULE.ACTION.DENY,
      ...this.processProfileRuleForm.value,
    };

    newData.group = this.processProfileRuleForm.controls.group.value;

    this.processProfileRulesService
      .updateProcessProfileRules(
        this.type === GlobalConstant.MODAL_OP.ADD
          ? GlobalConstant.CRUD.C
          : GlobalConstant.CRUD.U,
        this.processProfileRuleForm.controls.group.value,
        newData,
        this.data.oldData || {},
        this.data.source === GlobalConstant.NAV_SOURCE.FED_POLICY ?
        GlobalConstant.SCOPE.FED :
        GlobalConstant.SCOPE.LOCAL
      )
      .subscribe(
        response => {
          let msgTitle = this.type === GlobalConstant.MODAL_OP.ADD ?
            this.translate.instant('group.profile.ADD_OK') :
            this.translate.instant('group.profile.EDIT_OK');
          this.notificationService.open(msgTitle);
          setTimeout(() => {
            this.data.getProcessProfileRules(this.data.groupName);
          }, 1000);
          this.onCancel();
        },
        error => {
          let msgTitle = this.type === GlobalConstant.MODAL_OP.ADD ?
            this.translate.instant('group.profile.ADD_NG') :
            this.translate.instant('group.profile.EDIT_NG');
          this.notificationService.openError(error, msgTitle);
        }
      );
  }
}
