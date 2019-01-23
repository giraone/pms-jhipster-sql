import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IEmployeeName } from 'app/shared/model/employee-name.model';
import { EmployeeNameService } from './employee-name.service';
import { IEmployee } from 'app/shared/model/employee.model';
import { EmployeeService } from 'app/entities/employee';

@Component({
    selector: 'jhi-employee-name-update',
    templateUrl: './employee-name-update.component.html'
})
export class EmployeeNameUpdateComponent implements OnInit {
    employeeName: IEmployeeName;
    isSaving: boolean;

    employees: IEmployee[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected employeeNameService: EmployeeNameService,
        protected employeeService: EmployeeService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ employeeName }) => {
            this.employeeName = employeeName;
        });
        this.employeeService.query().subscribe(
            (res: HttpResponse<IEmployee[]>) => {
                this.employees = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.employeeName.id !== undefined) {
            this.subscribeToSaveResponse(this.employeeNameService.update(this.employeeName));
        } else {
            this.subscribeToSaveResponse(this.employeeNameService.create(this.employeeName));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IEmployeeName>>) {
        result.subscribe((res: HttpResponse<IEmployeeName>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackEmployeeById(index: number, item: IEmployee) {
        return item.id;
    }
}
