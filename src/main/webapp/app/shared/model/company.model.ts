export interface ICompany {
    id?: number;
    name?: string;
    departmentId?: string;
    postalCode?: string;
    city?: string;
    streetAddress?: string;
}

export class Company implements ICompany {
    constructor(
        public id?: number,
        public name?: string,
        public departmentId?: string,
        public postalCode?: string,
        public city?: string,
        public streetAddress?: string
    ) {}
}
