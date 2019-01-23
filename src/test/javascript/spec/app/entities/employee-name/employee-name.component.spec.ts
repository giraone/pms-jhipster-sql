/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { PmssqlTestModule } from '../../../test.module';
import { EmployeeNameComponent } from 'app/entities/employee-name/employee-name.component';
import { EmployeeNameService } from 'app/entities/employee-name/employee-name.service';
import { EmployeeName } from 'app/shared/model/employee-name.model';

describe('Component Tests', () => {
    describe('EmployeeName Management Component', () => {
        let comp: EmployeeNameComponent;
        let fixture: ComponentFixture<EmployeeNameComponent>;
        let service: EmployeeNameService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [PmssqlTestModule],
                declarations: [EmployeeNameComponent],
                providers: []
            })
                .overrideTemplate(EmployeeNameComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(EmployeeNameComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(EmployeeNameService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new EmployeeName(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.employeeNames[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
