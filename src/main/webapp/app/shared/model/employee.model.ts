import { Moment } from 'moment';
import { ICompany } from 'app/shared/model//company.model';

export const enum GenderType {
    MALE = 'MALE',
    FEMALE = 'FEMALE'
}

export interface IEmployee {
    id?: number;
    surname?: string;
    givenName?: string;
    dateOfBirth?: Moment;
    gender?: GenderType;
    postalCode?: string;
    city?: string;
    streetAddress?: string;
    company?: ICompany;
}

export class Employee implements IEmployee {
    constructor(
        public id?: number,
        public surname?: string,
        public givenName?: string,
        public dateOfBirth?: Moment,
        public gender?: GenderType,
        public postalCode?: string,
        public city?: string,
        public streetAddress?: string,
        public company?: ICompany
    ) {}
}
