export interface IEmployeeName {
    id?: number;
    key?: string;
    value?: string;
    ownerId?: number;
}

export class EmployeeName implements IEmployeeName {
    constructor(public id?: number, public key?: string, public value?: string, public ownerId?: number) {}
}
