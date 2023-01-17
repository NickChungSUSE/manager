import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgGridModule } from 'ag-grid-angular';
import { ProcessProfileRulesComponent } from './process-profile-rules.component';
import { NvCommonModule } from '@common/nvCommon.module';
import { QuickFilterModule } from '@components/quick-filter/quick-filter.module';
import { AddEditProcessProfileRuleModalComponent } from './partial/add-edit-process-profile-rule-modal/add-edit-process-profile-rule-modal.component';

@NgModule({
    declarations: [
        ProcessProfileRulesComponent,
        AddEditProcessProfileRuleModalComponent,
    ],
    imports: [
        CommonModule,
        NvCommonModule,
        QuickFilterModule,
        AgGridModule.withComponents([]),
    ],
    exports: [ProcessProfileRulesComponent]
})
export class ProcessProfileRulesModule {}
