/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { PmssqlTestModule } from '../../../test.module';
import { EmployeeNameUpdateComponent } from 'app/entities/employee-name/employee-name-update.component';
import { EmployeeNameService } from 'app/entities/employee-name/employee-name.service';
import { EmployeeName } from 'app/shared/model/employee-name.model';

describe('Component Tests', () => {
    describe('EmployeeName Management Update Component', () => {
        let comp: EmployeeNameUpdateComponent;
        let fixture: ComponentFixture<EmployeeNameUpdateComponent>;
        let service: EmployeeNameService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [PmssqlTestModule],
                declarations: [EmployeeNameUpdateComponent]
            })
                .overrideTemplate(EmployeeNameUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(EmployeeNameUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(EmployeeNameService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new EmployeeName(123);
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.employeeName = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new EmployeeName();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.employeeName = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
