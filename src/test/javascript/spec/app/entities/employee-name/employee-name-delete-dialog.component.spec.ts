/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { PmssqlTestModule } from '../../../test.module';
import { EmployeeNameDeleteDialogComponent } from 'app/entities/employee-name/employee-name-delete-dialog.component';
import { EmployeeNameService } from 'app/entities/employee-name/employee-name.service';

describe('Component Tests', () => {
    describe('EmployeeName Management Delete Component', () => {
        let comp: EmployeeNameDeleteDialogComponent;
        let fixture: ComponentFixture<EmployeeNameDeleteDialogComponent>;
        let service: EmployeeNameService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [PmssqlTestModule],
                declarations: [EmployeeNameDeleteDialogComponent]
            })
                .overrideTemplate(EmployeeNameDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(EmployeeNameDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(EmployeeNameService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
