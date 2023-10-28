import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreditDecisionComponent } from './credit-decision.component';



@NgModule({
  declarations: [CreditDecisionComponent],
  imports: [
    CommonModule
  ],
  exports: [
    CreditDecisionComponent
  ]
})
export class CreditDecisionModule { }
