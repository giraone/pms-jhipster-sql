import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PmssqlCompanyModule } from './company/company.module';
import { PmssqlEmployeeModule } from './employee/employee.module';
import { PmssqlEmployeeNameModule } from './employee-name/employee-name.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        PmssqlCompanyModule,
        PmssqlEmployeeModule,
        PmssqlEmployeeNameModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlEntityModule {}
