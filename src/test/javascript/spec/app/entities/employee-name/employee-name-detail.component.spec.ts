/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PmssqlTestModule } from '../../../test.module';
import { EmployeeNameDetailComponent } from 'app/entities/employee-name/employee-name-detail.component';
import { EmployeeName } from 'app/shared/model/employee-name.model';

describe('Component Tests', () => {
    describe('EmployeeName Management Detail Component', () => {
        let comp: EmployeeNameDetailComponent;
        let fixture: ComponentFixture<EmployeeNameDetailComponent>;
        const route = ({ data: of({ employeeName: new EmployeeName(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [PmssqlTestModule],
                declarations: [EmployeeNameDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(EmployeeNameDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(EmployeeNameDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.employeeName).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
