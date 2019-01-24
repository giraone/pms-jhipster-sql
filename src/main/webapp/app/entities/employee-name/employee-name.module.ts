import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PmssqlSharedModule } from 'app/shared';
import { EmployeeNameComponent, employeeNameRoute } from './';

const ENTITY_STATES = [...employeeNameRoute];

@NgModule({
    imports: [PmssqlSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [EmployeeNameComponent],
    entryComponents: [EmployeeNameComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlEmployeeNameModule {}
