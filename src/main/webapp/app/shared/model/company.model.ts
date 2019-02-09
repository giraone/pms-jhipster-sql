import { IUser } from 'app/core/user/user.model';

export interface ICompany {
    id?: number;
    externalId?: string;
    name?: string;
    postalCode?: string;
    city?: string;
    streetAddress?: string;
    users?: IUser[];
}

export class Company implements ICompany {
    constructor(
        public id?: number,
        public externalId?: string,
        public name?: string,
        public postalCode?: string,
        public city?: string,
        public streetAddress?: string,
        public users?: IUser[]
    ) {}
}
