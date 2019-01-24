export interface IEmployeeName {
    ownerId?: number;
    nameKey?: string;
    nameValue?: string;

    compoundId(): String;
}

export class EmployeeName implements IEmployeeName {
    constructor(public ownerId?: number, public nameKey?: string, public nameValue?: string) {}

    compoundId(): String {
        return this.ownerId + '.' + this.nameKey;
    }
}
