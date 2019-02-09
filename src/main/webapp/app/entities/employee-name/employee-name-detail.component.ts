import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEmployeeName } from 'app/shared/model/employee-name.model';

@Component({
    selector: 'jhi-employee-name-detail',
    templateUrl: './employee-name-detail.component.html'
})
export class EmployeeNameDetailComponent implements OnInit {
    employeeName: IEmployeeName;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ employeeName }) => {
            this.employeeName = employeeName;
        });
    }

    previousState() {
        window.history.back();
    }
}
