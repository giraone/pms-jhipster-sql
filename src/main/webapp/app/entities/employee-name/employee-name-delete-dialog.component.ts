import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IEmployeeName } from 'app/shared/model/employee-name.model';
import { EmployeeNameService } from './employee-name.service';

@Component({
    selector: 'jhi-employee-name-delete-dialog',
    templateUrl: './employee-name-delete-dialog.component.html'
})
export class EmployeeNameDeleteDialogComponent {
    employeeName: IEmployeeName;

    constructor(
        protected employeeNameService: EmployeeNameService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.employeeNameService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'employeeNameListModification',
                content: 'Deleted an employeeName'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-employee-name-delete-popup',
    template: ''
})
export class EmployeeNameDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ employeeName }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(EmployeeNameDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.employeeName = employeeName;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
