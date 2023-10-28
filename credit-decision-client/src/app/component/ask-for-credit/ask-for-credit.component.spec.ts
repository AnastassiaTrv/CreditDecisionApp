import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AskForCreditComponent } from './ask-for-credit.component';

describe('AskForCreditComponent', () => {
  let component: AskForCreditComponent;
  let fixture: ComponentFixture<AskForCreditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AskForCreditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AskForCreditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
