import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from '../../core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Employee } from '../../shared/model/employee.model';
import { EmployeeService } from '../../entities/employee/employee.service';
import { EmployeeComponent } from './employee.component';
import { IEmployee } from '../../shared/model/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeResolve implements Resolve<IEmployee> {
    constructor(private service: EmployeeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Employee> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Employee>) => response.ok),
                map((employee: HttpResponse<Employee>) => employee.body)
            );
        }
        return of(new Employee());
    }
}

export const employeeRoute: Routes = [
    {
        path: 'employee-query',
        component: EmployeeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'pmssqlApp.query.pageTitleMain'
        },
        canActivate: [UserRouteAccessService]
    }
];
