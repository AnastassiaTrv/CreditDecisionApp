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
  errorMsg: string;

  ngOnInit() { }

  askForCredit() {
    this.cleanupPreviousResponse();
    this.creditDecisionService.getCreditDecision(this.customerId, this.amount, this.period)
    .subscribe(
      decision => {
        this.decision = decision;
      },
      e => {
        this.decision = null;
        this.showInputError = true;
        if (e.error.status === 400 && e.error.message) {
          this.errorMsg = e.error.message;
        }
      });
  }

  disableButton() {
    return !this.customerId || !this.amount || !this.period;
  }

  cleanupPreviousResponse() {
    this.errorMsg = null;
    this.decision = null;
    this.showInputError = false;
  }

}
