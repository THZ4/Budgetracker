import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TransactionprotocolComponent } from './transactionprotocol/transactionprotocol.component';

export const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'transactions', component: TransactionprotocolComponent },
  //   { path: 'cards', component: CardsComponent },
  //   { path: 'bank-accounts', component: BankAccountsComponent },
  //   { path: 'notifications', component: NotificationsComponent },
  //   { path: 'settings', component: SettingsComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' },

  
];
