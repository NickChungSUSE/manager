import { Component, OnInit, SecurityContext, Input } from '@angular/core';
import { ErrorResponse, HierarchicalExposure } from '@common/types';
import { GlobalVariable } from '@common/variables/global.variable';
import {
  ColDef,
  GridApi,
  GridOptions,
  GridReadyEvent,
  RowNode,
} from 'ag-grid-community';
import { TranslateService } from '@ngx-translate/core';
import * as $ from 'jquery';
import { DomSanitizer } from '@angular/platform-browser';
import { UtilsService } from '@common/utils/app.utils';
import { MapConstant } from '@common/constants/map.constant';
import { GraphHttpService } from '@common/api/graph-http.service';
import { NotificationService } from '@services/notification.service';
import { ExposedServicePodGridActionCellComponent } from '@components/exposed-service-pod-grid/exposed-service-pod-grid-action-cell/exposed-service-pod-grid-action-cell.component';
import { ExposedServicepodGridServicepodCellComponent } from '@components/exposed-servicepod-conv-grid/exposed-servicepod-grid-servicepod-cell/exposed-servicepod-grid-servicepod-cell.component';
import { ExternalHostCellComponent } from '@components/exposed-servicepod-conv-grid/external-host-cell/external-host-cell.component';
import { ConversationEntryListComponent } from '@components/exposed-servicepod-conv-grid/conversation-entry-list/conversation-entry-list.component';
import { uuid } from '@common/utils/common.utils';
import { RegistryDetailsVulnerabilitiesCellComponent } from '@routes/registries/registry-details/registry-details-table/registry-details-vulnerabilities-cell/registry-details-vulnerabilities-cell.component';

@Component({
  selector: 'app-exposed-servicepod-conv-grid',
  templateUrl: './exposed-servicepod-conv-grid.component.html',
  styleUrls: ['./exposed-servicepod-conv-grid.component.scss'],
})
export class ExposedServicepodConvGridComponent implements OnInit {
  private readonly $win;
  private _exposures!: Array<HierarchicalExposure>;
  @Input() set exposures(exposure: Array<HierarchicalExposure>) {
    this._exposures = exposure;
    this.displayedExposure = this.preprocessHierarchicalData(this._exposures);
  }
  get exposures() {
    return this._exposures;
  }
  gridOptions!: GridOptions;
  gridApi!: GridApi;
  columnDefs!: ColDef[];
  displayedExposure!: Array<any>;

  constructor(
    private graphHttpService: GraphHttpService,
    private notificationService: NotificationService,
    private translate: TranslateService,
    private sanitizer: DomSanitizer,
    private utils: UtilsService
  ) {
    this.$win = $(GlobalVariable.window);
  }

  ngOnInit(): void {
    this.setGrid();
  }

  setGrid = () => {
    this.columnDefs = [
      {
        headerName: this.translate.instant(
          'dashboard.body.panel_title.SERVICE'
        ),
        field: 'service',
        cellRenderer: 'serviceCellRenderer',
        width: 180,
        sortable: false,
      },
      {
        headerName: 'Pods',
        field: 'children',
        valueFormatter: params => {
          return params.value.length;
        },
        width: 70,
      },
      {
        headerName: this.translate.instant(
          'dashboard.body.panel_title.VULNERABILITIES'
        ),
        field: 'vulnerabilities',
        cellRenderer: 'vulnerabilitiesCellRenderer',
        width: 110,
        maxWidth: 110,
        minWidth: 110,
        sortable: false,
      },
      {
        headerName: 'Parent ID',
        field: 'parent_id',
        hide: true,
      },
      {
        headerName: this.translate.instant(
          'dashboard.body.panel_title.POLICY_MODE'
        ),
        field: 'policy_mode',
        cellRenderer: params => {
          let mode = '';
          if (params.data && params.value) {
            mode = this.utils.getI18Name(params.value);
            let labelCode = MapConstant.colourMap[params.value];
            if (!labelCode) return null;
            else
              return `<span class='type-label policy_mode ${labelCode}'>${this.sanitizer.sanitize(
                SecurityContext.HTML,
                mode
              )}</span>`;
          }
          return null;
        },
        width: 110,
        maxWidth: 110,
        minWidth: 110,
        sortable: false,
      },
      // {
      //   headerName: this.translate.instant(
      //     'dashboard.body.panel_title.APPLICATIONS'
      //   ),
      //   field: 'applications',
      //   cellRenderer: params => {
      //     if (params.data) {
      //       if (params.value) {
      //         return this.sanitizer.sanitize(
      //           SecurityContext.HTML,
      //           params.data.ports
      //             ? params.value.concat(params.data.ports).join(', ')
      //             : params.value.join(', ')
      //         );
      //       }
      //     }
      //     return null;
      //   },
      //   width: 100,
      //   sortable: false,
      // },
      {
        headerName: this.translate.instant(
          'dashboard.body.panel_title.POLICY_ACTION'
        ),
        field: 'policy_action',
        cellRenderer: params => {
          if (params.data) {
            return `<span ng-class='{\'policy-remove\': data.remove}'
                  class='action-label px-1 ${
                    MapConstant.colourMap[
                      params.data.policy_action.toLowerCase()
                    ]
                  }'>
                  ${this.sanitizer.sanitize(
                    SecurityContext.HTML,
                    this.translate.instant(
                      'policy.action.' + params.data.policy_action.toUpperCase()
                    )
                  )}
                </span>`;
          }
          return null;
        },
        width: 80,
        maxWidth: 80,
        minWidth: 80,
        sortable: false,
      },
    ];

    this.gridOptions = this.utils.createGridOptions(this.columnDefs, this.$win);
    this.gridOptions = {
      ...this.gridOptions,
      defaultColDef: {
        ...this.gridOptions.defaultColDef,
        flex: 1,
        autoHeight: true,
        sortable: true,
        resizable: true,
        cellClass: ['d-flex', 'align-items-center', 'cell-wrap-text'],
      },
      onColumnResized: params => {
        if (params && params.api) params.api.resetRowHeights();
      },
      isExternalFilterPresent: () => true,
      doesExternalFilterPass: params =>
        !params.data.parent_id || params.data.visible,
      getRowId: params => params.data.id,
      getRowHeight: params => (!!params.data.parent_id ? 100 : 30),
      isFullWidthCell: node => !!node.data.parent_id,
      fullWidthCellRenderer: 'conversationEntryListRenderer',
      suppressMaintainUnsortedOrder: true,
      suppressScrollOnNewData: true,
      components: {
        vulnerabilitiesCellRenderer:
          RegistryDetailsVulnerabilitiesCellComponent,
        serviceCellRenderer: ExposedServicepodGridServicepodCellComponent,
        actionCellRenderer: ExposedServicePodGridActionCellComponent,
        conversationEntryListRenderer: ConversationEntryListComponent,
      },
    };
  };

  onGridReady = (params: GridReadyEvent): void => {
    this.gridApi = params.api;
    setTimeout(() => {
      this.gridApi.sizeColumnsToFit();
      this.gridApi.getDisplayedRowAtIndex(0)?.setSelected(true);
    });
  };

  onResize = (): void => {
    this.gridApi?.sizeColumnsToFit();
  };

  preprocessHierarchicalData = (
    exposures: Array<HierarchicalExposure>
  ): Array<any> => {
    let res: Array<any> = [];
    exposures.forEach(exposure => {
      const parent_id = uuid();
      if (exposure.entries) {
        const child_id = uuid();
        res.push({
          id: parent_id,
          child_id,
          ...exposure,
          pods: exposure.children.length,
          visible: false,
        });
        res.push({
          id: child_id,
          parent_id,
          ...exposure,
          pods: exposure.children.length,
          visible: false,
        });
      } else {
        res.push({
          id: parent_id,
          ...exposure,
          pods: exposure.children.length,
          visible: true,
        });
      }
    });
    console.log('preprocessHierarchicalData', res);
    return res;
  };
}
