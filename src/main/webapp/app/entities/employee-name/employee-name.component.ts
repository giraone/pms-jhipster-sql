import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IEmployeeName } from 'app/shared/model/employee-name.model';
import { AccountService } from 'app/core';
import { EmployeeNameService } from './employee-name.service';

@Component({
    selector: 'jhi-employee-name',
    templateUrl: './employee-name.component.html'
})
export class EmployeeNameComponent implements OnInit, OnDestroy {
    employeeNames: IEmployeeName[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected employeeNameService: EmployeeNameService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.employeeNameService.query().subscribe(
            (res: HttpResponse<IEmployeeName[]>) => {
                this.employeeNames = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInEmployeeNames();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IEmployeeName) {
        return item.id;
    }

    registerChangeInEmployeeNames() {
        this.eventSubscriber = this.eventManager.subscribe('employeeNameListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
