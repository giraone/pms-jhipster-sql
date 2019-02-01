import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription, Subject } from 'rxjs';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { IEmployee } from 'app/shared/model/employee.model';
import { ICompany } from '../../shared/model/company.model';
import { AccountService } from '../../core';

import { ITEMS_PER_PAGE } from '../../shared';
import { EmployeeService } from './employee.service';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'jhi-employee',
    templateUrl: './employee.component.html'
})
export class EmployeeComponent implements OnInit, OnDestroy {
    input: String;
    inputSubject: Subject<String> = new Subject();
    employees: IEmployee[];
    currentAccount: any;
    currentAccountIsAdmin: boolean;
    currentCompanies: ICompany[];
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    queryCount: any;
    reverse: any;
    totalItems: number;

    constructor(
        protected employeeService: EmployeeService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected parseLinks: JhiParseLinks,
        protected accountService: AccountService
    ) {
        this.input = '';
        this.employees = [];
        this.currentCompanies = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.totalItems = 0;
        this.inputSubject
            .pipe(
                debounceTime(200),
                distinctUntilChanged()
            )
            .subscribe(value => {
                this.reset();
            });
    }

    load() {
        const trimed = this.input.trim();
        if (trimed.length < 2) {
            return;
        }
        let surnameValue: String;
        let surnameSearchModeValue: String;
        if (trimed.charAt(0) === '~') {
            surnameValue = trimed.substr(1);
            surnameSearchModeValue = 'PHONETIC';
        } else if (trimed.charAt(0) === '-') {
            surnameValue = trimed.substr(1);
            surnameSearchModeValue = 'PREFIX_LOWERCASE';
        } else if (trimed.charAt(0) === '=') {
            surnameValue = trimed.substr(1);
            surnameSearchModeValue = 'EXACT';
        } else {
            surnameValue = trimed;
            surnameSearchModeValue = 'PREFIX_REDUCED';
        }
        if (surnameValue.length < 2) {
            return;
        }
        this.employeeService
            .query({
                companyExternalId: this.getExternalCompanyId(),
                surname: surnameValue,
                surnameSearchMode: surnameSearchModeValue,
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<IEmployee[]>) => this.paginateEmployees(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    filterChanged(event) {
        this.inputSubject.next(event);
    }

    reset() {
        this.page = 0;
        this.employees = [];
        this.load();
    }

    loadPage(page) {
        this.page = page;
        this.load();
    }

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            this.currentAccountIsAdmin = this.accountService.hasAnyAuthority(['ROLE_ADMIN']);
        });
        this.employeeService.findCompanies().subscribe(
            (res: HttpResponse<ICompany[]>) => {
                this.currentCompanies = res.body;
                this.load();
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );

        this.registerChangeInEmployees();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IEmployee) {
        return item.id;
    }

    registerChangeInEmployees() {
        this.eventSubscriber = this.eventManager.subscribe('employeeListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    isAdmin() {
        return this.currentAccountIsAdmin;
    }

    protected paginateEmployees(data: IEmployee[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        if (this.page === 0) {
            this.employees.length = 0;
        }
        for (let i = 0; i < data.length; i++) {
            this.employees.push(data[i]);
        }
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    protected getExternalCompanyId() {
        if (this.currentCompanies == null || this.currentCompanies.length === 0) {
            return 'none';
        }
        return this.currentCompanies[0].externalId;
    }
}
