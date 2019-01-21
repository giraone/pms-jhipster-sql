import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from '../../shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared';
import { IEmployee } from '../../shared/model/employee.model';

type EntityResponseType = HttpResponse<IEmployee>;
type EntityArrayResponseType = HttpResponse<IEmployee[]>;

@Injectable({ providedIn: 'root' })
export class EmployeeService {
    public resourceUrl = SERVER_API_URL + 'domain-api/employees';
    // public resourceUrl = 'http://localhost:8080/' + 'domain-api/employees';

    constructor(protected http: HttpClient) {}

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IEmployee>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        req.companyExternalId = 'l-00000060';
        const options = createRequestOption(req);
        return this.http
            .get<IEmployee[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(employee: IEmployee): IEmployee {
        const copy: IEmployee = Object.assign({}, employee, {
            dateOfBirth: employee.dateOfBirth != null && employee.dateOfBirth.isValid() ? employee.dateOfBirth.format(DATE_FORMAT) : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.dateOfBirth = res.body.dateOfBirth != null ? moment(res.body.dateOfBirth) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((employee: IEmployee) => {
                employee.dateOfBirth = employee.dateOfBirth != null ? moment(employee.dateOfBirth) : null;
            });
        }
        return res;
    }
}