import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PmssqlSharedModule } from '../../shared';
import { EmployeeComponent, employeeRoute } from './';

const ENTITY_STATES = [...employeeRoute];

@NgModule({
    imports: [PmssqlSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [EmployeeComponent],
    entryComponents: [EmployeeComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlEmployeeQueryModule {}
