import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from "@angular/core";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import { ActivityState, PopupState } from "@common/types/network-activities/activityState";
import { UntypedFormControl, UntypedFormGroup } from "@angular/forms";
import { Observable } from "rxjs";
import {
  GraphItem,
  GroupItem
} from "@common/types/network-activities/blacklist";
import { map } from "rxjs/operators";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatChipInputEvent } from "@angular/material/chips";
import { AdvancedFilter } from "@common/types/network-activities/advancedFilter";
import { GraphSettings, Settings } from "@common/types/network-activities/settings";

@Component({
  selector: 'app-advanced-filter',
  templateUrl: './advanced-filter.component.html',
  styleUrls: ['./advanced-filter.component.scss']
})
export class AdvancedFilterComponent implements OnInit {
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  private _popupState: ActivityState;
  namespaceCtrl = new UntypedFormControl();
  filteredDomains!: Observable<GraphItem[]>;
  groupCtrl = new UntypedFormControl();
  filteredGroups!: Observable<GroupItem[]>;

  advFilterForm!: UntypedFormGroup;
  @ViewChild('domainInput') domainInput!: ElementRef<HTMLInputElement>;
  @ViewChild('groupInput') groupInput!: ElementRef<HTMLInputElement>;

  showButton = {};
  showGrpButton = {};

  private _advFilter!: AdvancedFilter;
  get advFilter(): AdvancedFilter {
    return this._advFilter;
  }

  @Input()
  set advFilter(value: AdvancedFilter) {
    this._advFilter = value;
  }

  private _settings!: Settings

  get settings(): Settings {
    return this._settings;
  }

  @Input()
  set settings(value: Settings) {
    this._settings = value;
  }

  @Output()
  onReset: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  onApply: EventEmitter<GraphSettings> = new EventEmitter<GraphSettings>();

  get popupState(): ActivityState {
    return this._popupState;
  }

  @Input()
  set popupState(value: ActivityState) {
    this._popupState = value;
  }

  private _domains: GraphItem[] = [];
  get domains(): GraphItem[] {
    return this._domains;
  }

  @Input()
  set domains(value: GraphItem[]) {
    this._domains = value;
  }

  private _groups: GroupItem[] = [];
  get groups(): { name: string; displayName: string }[] {
    return this._groups;
  }

  @Input()
  set groups(value: GroupItem[]) {
    this._groups = value;
  }
  constructor() {
    this._popupState = new ActivityState(PopupState.onInit);
    this.filteredDomains = this.namespaceCtrl.valueChanges.pipe(
      map((domain: GraphItem) =>
        domain ? this._filter(domain) : this.domains.slice()
      )
    );
    this.filteredGroups = this.groupCtrl.valueChanges.pipe(
      map((group: string) =>
        group ? this._groupFilter(group) : this.groups.slice()
      )
    );
  }

  ngOnInit(): void {
    const filter = this.advFilter;
    const settings = this.settings;
    this.advFilterForm = new UntypedFormGroup({
      domains: new UntypedFormControl(filter.domains),
      selectedGroups: new UntypedFormControl(filter.groups),
      vulnerabilityType: new UntypedFormControl(filter.cve),
      protocols: new UntypedFormGroup({
        tcp: new UntypedFormControl(filter.protocol.tcp),
        udp: new UntypedFormControl(filter.protocol.udp),
        icmp: new UntypedFormControl(filter.protocol.icmp),
      }),
      riskType: new UntypedFormControl(filter.risk),
      settings: new UntypedFormGroup({
        showSysNode: new UntypedFormControl(settings.showSysNode),
        showSysApp: new UntypedFormControl(settings.showSysApp),
        persistent: new UntypedFormControl(settings.persistent),
        gpuEnabled: new UntypedFormControl(settings.gpuEnabled),
      }),
      }
    );
  }

  reset() {
    this.advFilter = {
      domains: [],
      groups: [],
      policyMode: {
        discover: true,
        monitor: true,
        protect: true,
      },
      cve: 'all',
      protocol: {
        tcp: true,
        udp: true,
        icmp: true,
      },
      risk: 'all',
    };
    this.settings = {
      showSysNode: false,
      showSysApp: false,
      hiddenDomains: [],
      hiddenGroups: [],
      persistent: false,
      gpuEnabled: false,
    };
    this.onReset.emit('reset');
  }

  apply() {

    this.advFilter.domains = this.advFilterForm.value.domains;
    this.advFilter.groups = this.advFilterForm.value.selectedGroups;
    this.advFilter.cve = this.advFilterForm.value.vulnerabilityType;
    this.advFilter.protocol = this.advFilterForm.value.protocols;
    this.advFilter.risk = this.advFilterForm.value.riskType;
    this.settings.persistent = this.advFilterForm.value.settings.persistent;
    this.settings.showSysNode = this.advFilterForm.value.settings.showSysNode;
    this.settings.showSysApp = this.advFilterForm.value.settings.showSysApp;
    this.settings.gpuEnabled = this.advFilterForm.value.settings.gpuEnabled;

    this.onApply.emit({advFilter: this.advFilter, settings: this.settings});
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.domainInput.nativeElement.value = '';
    if (this.advFilterForm.controls.domains.value.includes(event.option.value))
      return;
    this.advFilterForm.controls.domains.value.push(event.option.value);
    this.namespaceCtrl.setValue(null);
  }

  add(event: MatChipInputEvent): void {
    this.addItem(event, this._domains, "domain");
    this.namespaceCtrl.setValue(null);
  }

  remove(domain: string, index: number): void {
    this.advFilterForm.controls.domains.value.splice(index, 1);
  }

  groupSelected(event: MatAutocompleteSelectedEvent): void {
    this.groupInput.nativeElement.value = '';
    if (this.advFilterForm.controls.selectedGroups.value.includes(event.option.value))
      return;
    this.advFilterForm.controls.selectedGroups.value.push(event.option.value);
    this.groupCtrl.setValue(null);
  }

  addGroup(event: MatChipInputEvent): void {
    this.addItem(event, this._groups, "group");
    this.groupCtrl.setValue(null);
  }

  removeGroup(group: string, index: number) {
    this.advFilterForm.controls.selectedGroups.value.splice(index, 1);
  }

  private _filter(value: any): GraphItem[] {
    const filterValue =
      value === null || value instanceof Object ? '' : value.toLowerCase();

    const matches = this._domains.filter(domain =>
      domain.name.toLowerCase().includes(filterValue)
    );
    const formValue = this.advFilterForm.controls.domains.value;
    return formValue === null
      ? matches
      : matches.filter(x => !formValue.find(y => y.name === x.name));
  }

  private _groupFilter(value: any): GroupItem[] {
    const filterValue =
      value === null || value instanceof Object ? '' : value.toLowerCase();

    const matches = this.groups.filter(group =>
      group.name.toLowerCase().includes(filterValue)
    );
    const formValue = this.advFilterForm.controls.selectedGroups.value;
    return formValue === null
      ? matches
      : matches.filter(x => !formValue.find(y => y.name === x.name));
  }

  private addItem(event: MatChipInputEvent, list: any [], type: string): void {
    const selectedItems = {
      "domain": this.advFilterForm.controls.domains,
      "group": this.advFilterForm.controls.selectedGroups,
    };
    const value = (event.value || '').trim();

    const matches = list.filter(
      item => item.name.toLowerCase() === value
    );
    const formValue = selectedItems[type].value;
    const matchesNotYetSelected =
      formValue === null
        ? matches
        : matches.filter(x => !formValue.find(y => y.name === x.name));
    if (matchesNotYetSelected.length === 1)
      selectedItems[type].value.push(matchesNotYetSelected[0]);

    event.chipInput?.clear();
  }
}
