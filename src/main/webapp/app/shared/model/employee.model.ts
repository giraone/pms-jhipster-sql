import { Moment } from 'moment';

export const enum GenderType {
    UNKNOWN = 'UNKNOWN',
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
    companyId?: number;
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
        public companyId?: number
    ) {}
}
