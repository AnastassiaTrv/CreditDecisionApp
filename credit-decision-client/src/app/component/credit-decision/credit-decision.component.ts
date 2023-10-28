import { Component, Input, OnInit } from '@angular/core';
import { CreditDecision } from 'src/app/model/CreditDecision';

@Component({
  selector: 'app-credit-decision',
  templateUrl: './credit-decision.component.html',
  styleUrls: ['./credit-decision.component.css']
})
export class CreditDecisionComponent implements OnInit {

  @Input() decision: CreditDecision;

  constructor() { }

  ngOnInit() {
  }

  getTableClass() {
    return {
      'table-success': this.decision.status === 'APROOVED',
      'table-danger': this.decision.status === 'REJECTED',
      'table-warning': this.decision.status === 'UNDEFINED',
    }
  }

}
