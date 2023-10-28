import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AskForCreditComponent } from './ask-for-credit.component';
import { CreditDecisionModule } from '../credit-decision/credit-decision.module';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [AskForCreditComponent],
  imports: [
    CommonModule,
    FormsModule,
    CreditDecisionModule
  ],
  exports: [
    AskForCreditComponent
  ]
})
export class AskForCreditModule { }
