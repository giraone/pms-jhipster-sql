import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { PmssqlCompanyModule } from './company/company.module';
import { PmssqlEmployeeModule } from './employee/employee.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        PmssqlCompanyModule,
        PmssqlEmployeeModule
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlEntityModule {}
