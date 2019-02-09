import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PmssqlSharedModule } from 'app/shared';
import {
    EmployeeNameComponent,
    EmployeeNameDetailComponent,
    EmployeeNameUpdateComponent,
    EmployeeNameDeletePopupComponent,
    EmployeeNameDeleteDialogComponent,
    employeeNameRoute,
    employeeNamePopupRoute
} from './';

const ENTITY_STATES = [...employeeNameRoute, ...employeeNamePopupRoute];

@NgModule({
    imports: [PmssqlSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        EmployeeNameComponent,
        EmployeeNameDetailComponent,
        EmployeeNameUpdateComponent,
        EmployeeNameDeleteDialogComponent,
        EmployeeNameDeletePopupComponent
    ],
    entryComponents: [
        EmployeeNameComponent,
        EmployeeNameUpdateComponent,
        EmployeeNameDeleteDialogComponent,
        EmployeeNameDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PmssqlEmployeeNameModule {}
