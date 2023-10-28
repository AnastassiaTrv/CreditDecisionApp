import { Component, OnInit } from '@angular/core';
import { CreditDecisionService } from 'src/app/service/credit-decision.service';

@Component({
  selector: 'app-ask-for-credit',
  templateUrl: './ask-for-credit.component.html',
  styleUrls: ['./ask-for-credit.component.css']
})
export class AskForCreditComponent implements OnInit {

  constructor(private creditDecisionService: CreditDecisionService) { }

  customerId: string;
  amount: number;
  period: number;
  decision: any;
  showInputError = false;

  ngOnInit() { }

  askForCredit() {
    this.cleanupPreviousResponse();
    this.creditDecisionService.getCreditDecision(this.customerId, this.amount, this.period)
    .subscribe(
      decision => {
        this.decision = decision;
      },
      error => {
        this.decision = null;
        this.showInputError = error.status === 400; // just wanted to handle a bad request case somehow
      });
  }

  disableButton() {
    return !this.customerId || !this.amount || !this.period;
  }

  cleanupPreviousResponse() {
    this.decision = null;
    this.showInputError = false;
  }

}
