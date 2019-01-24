export interface IEmployeeName {
    ownerId?: number;
    nameKey?: string;
    nameValue?: string;
}

export class EmployeeName implements IEmployeeName {
    constructor(public ownerId?: number, public nameKey?: string, public nameValue?: string) {}
}
