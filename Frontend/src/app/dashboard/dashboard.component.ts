import { Component, OnInit, ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ChartOptions, ChartData } from 'chart.js';
import { Transaction, TransactionService } from '../transaction.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [BaseChartDirective, CommonModule, FormsModule],
})
export class DashboardComponent implements OnInit {
  chartType: 'bar' = 'bar';

  chartData: ChartData<'bar'> = {
    labels: [
      'Jan',
      'Feb',
      'Mär',
      'Apr',
      'Mai',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Okt',
      'Nov',
      'Dez',
    ],
    datasets: [
      {
        label: 'Einnahmen',
        data: Array(12).fill(0),
        backgroundColor: '#1abc9c',
      },
      {
        label: 'Ausgaben',
        data: Array(12).fill(0),
        backgroundColor: '#e74c3c',
      },
    ],
  };

  chartOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: {
      legend: { position: 'top' },
      title: { display: true, text: 'Monatliche Übersicht' },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  constructor(private txService: TransactionService) {}

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  latestTransactions: Transaction[] = [];
  totalIncome = 0;
  totalExpenses = 0;
  balance = 0;
  selectedFilter: 'ALL' | 'INCOME' | 'EXPENSE' = 'ALL';
  filteredTransactions: Transaction[] = [];

  ngOnInit(): void {
    this.txService.getAll().subscribe((transactions: Transaction[]) => {
      const incomePerMonth = Array(12).fill(0);
      const expensePerMonth = Array(12).fill(0);
      this.totalIncome = 0;
      this.totalExpenses = 0;

      transactions.forEach((tx) => {
        const month = new Date(tx.date).getMonth();
        const type = tx.type?.toUpperCase();

        if (type === 'INCOME') {
          incomePerMonth[month] += tx.amount;
          this.totalIncome += tx.amount;
        } else if (type === 'EXPENSE') {
          expensePerMonth[month] += tx.amount;
          this.totalExpenses += tx.amount;
        }
      });

      this.chartData.datasets[0].data = incomePerMonth;
      this.chartData.datasets[1].data = expensePerMonth;

      this.latestTransactions = transactions
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
        .slice(0, 10);

      this.balance = this.totalIncome - this.totalExpenses;

      this.filterTransactions();

      this.chart?.update();
    });
  }

  getColor(type: string): string {
    return type === 'INCOME' ? 'green' : 'red';
  }

  filterTransactions() {
    if (this.selectedFilter === 'ALL') {
      this.filteredTransactions = [...this.latestTransactions];
    } else {
      this.filteredTransactions = this.latestTransactions.filter(
        (tx) => tx.type === this.selectedFilter
      );
    }
  }
}
