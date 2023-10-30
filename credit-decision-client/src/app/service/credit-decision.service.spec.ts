import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CreditDecisionService } from './credit-decision.service';

describe('CreditDecisionService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [ HttpClientTestingModule ]
  }));

  it('should be created', () => {
    const service: CreditDecisionService = TestBed.get(CreditDecisionService);
    expect(service).toBeTruthy();
  });
});
