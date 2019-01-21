import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PmssqlEmployeeQueryModule } from './employee/employee.module';

@NgModule({
    // prettier-ignore
    imports: [
        PmssqlEmployeeQueryModule
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlDomainModule {}
