import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AskForCreditComponent } from './ask-for-credit.component';
import { MockComponent } from 'ng-mocks';
import { CreditDecisionComponent } from '../credit-decision/credit-decision.component';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { CreditDecisionService } from 'src/app/service/credit-decision.service';

describe('AskForCreditComponent', () => {
  let component: AskForCreditComponent;
  let fixture: ComponentFixture<AskForCreditComponent>;
  const creditDecisionService = {
    getCreditDecision: () => {
      return new Observable();
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AskForCreditComponent, MockComponent(CreditDecisionComponent) ],
      imports: [ FormsModule ],
      providers: [
        { provide: CreditDecisionService, useValue: creditDecisionService }
      ]
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
