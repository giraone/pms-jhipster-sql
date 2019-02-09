export interface IEmployeeName {
    id?: number;
    ownerId?: number;
    nameKey?: string;
    nameValue?: string;
}

export class EmployeeName implements IEmployeeName {
    constructor(public id?: number, public ownerId?: number, public nameKey?: string, public nameValue?: string) {}
}
