import { TestBed } from '@angular/core/testing';

import { CreditDecisionService } from './credit-decision.service';

describe('CreditDecisionService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CreditDecisionService = TestBed.get(CreditDecisionService);
    expect(service).toBeTruthy();
  });
});
