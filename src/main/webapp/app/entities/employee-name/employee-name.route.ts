import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { EmployeeName } from 'app/shared/model/employee-name.model';
import { EmployeeNameService } from './employee-name.service';
import { EmployeeNameComponent } from './employee-name.component';
import { EmployeeNameDetailComponent } from './employee-name-detail.component';
import { EmployeeNameUpdateComponent } from './employee-name-update.component';
import { EmployeeNameDeletePopupComponent } from './employee-name-delete-dialog.component';
import { IEmployeeName } from 'app/shared/model/employee-name.model';

@Injectable({ providedIn: 'root' })
export class EmployeeNameResolve implements Resolve<IEmployeeName> {
    constructor(private service: EmployeeNameService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<EmployeeName> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<EmployeeName>) => response.ok),
                map((employeeName: HttpResponse<EmployeeName>) => employeeName.body)
            );
        }
        return of(new EmployeeName());
    }
}

export const employeeNameRoute: Routes = [
    {
        path: 'employee-name',
        component: EmployeeNameComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'pmssqlApp.employeeName.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'employee-name/:id/view',
        component: EmployeeNameDetailComponent,
        resolve: {
            employeeName: EmployeeNameResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pmssqlApp.employeeName.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'employee-name/new',
        component: EmployeeNameUpdateComponent,
        resolve: {
            employeeName: EmployeeNameResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pmssqlApp.employeeName.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'employee-name/:id/edit',
        component: EmployeeNameUpdateComponent,
        resolve: {
            employeeName: EmployeeNameResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pmssqlApp.employeeName.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const employeeNamePopupRoute: Routes = [
    {
        path: 'employee-name/:id/delete',
        component: EmployeeNameDeletePopupComponent,
        resolve: {
            employeeName: EmployeeNameResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pmssqlApp.employeeName.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
