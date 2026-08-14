import { Component } from '@angular/core';
import { BankingDashboardComponent } from './features/dashboard/banking-dashboard.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [BankingDashboardComponent],
  template: '<app-banking-dashboard />',
})
export class AppComponent {
}
