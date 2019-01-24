import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IEmployeeName } from 'app/shared/model/employee-name.model';

type EntityResponseType = HttpResponse<IEmployeeName>;
type EntityArrayResponseType = HttpResponse<IEmployeeName[]>;

@Injectable({ providedIn: 'root' })
export class EmployeeNameService {
    public resourceUrl = SERVER_API_URL + 'api/employee-names';

    constructor(protected http: HttpClient) {}

    create(employeeName: IEmployeeName): Observable<EntityResponseType> {
        return this.http.post<IEmployeeName>(this.resourceUrl, employeeName, { observe: 'response' });
    }

    update(employeeName: IEmployeeName): Observable<EntityResponseType> {
        return this.http.put<IEmployeeName>(this.resourceUrl, employeeName, { observe: 'response' });
    }

    find(compoundId: String): Observable<EntityResponseType> {
        return this.http.get<IEmployeeName>(`${this.resourceUrl}/${compoundId}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IEmployeeName[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(compoundId: String): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${compoundId}`, { observe: 'response' });
    }
}
