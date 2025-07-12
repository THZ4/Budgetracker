import { Component, OnInit, ViewChild } from '@angular/core';
import { Transaction, TransactionService } from '../transaction.service';
import { ChartData, ChartOptions } from 'chart.js';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transactionprotocol',
  imports: [CommonModule, FormsModule],
  templateUrl: './transactionprotocol.component.html',
  styleUrl: './transactionprotocol.component.css',
})
export class TransactionprotocolComponent implements OnInit {
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

  latestTransactions: Transaction[] = [];
  totalIncome = 0;
  totalExpenses = 0;
  balance = 0;

  ngOnInit(): void {
    this.txService.getAll().subscribe((transactions: Transaction[]) => {
      const incomePerMonth = Array(12).fill(0);
      const expensePerMonth = Array(12).fill(0);

      this.latestTransactions = transactions.sort(
        (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
      );

      this.totalIncome = 0;
      this.totalExpenses = 0;

      transactions.forEach((tx) => {
        const month = new Date(tx.date).getMonth();
        const type = tx.type?.toUpperCase();

        if (type === 'INCOME') {
          this.totalIncome += tx.amount;
          incomePerMonth[month] += tx.amount;
        } else if (type === 'EXPENSE') {
          this.totalExpenses += tx.amount;
          expensePerMonth[month] += tx.amount;
        }
      });

      this.balance = this.totalIncome - this.totalExpenses;
    });
  }

  showAddModal = false;

  newTransaction: Transaction = {
    id: 0,
    description: '',
    amount: 0,
    type: 'INCOME',
    category: '',
    date: new Date().toISOString().substring(0, 10), // yyyy-MM-dd
  };

  openAddModal() {
    this.showAddModal = true;
  }

  closeAddModal() {
    this.showAddModal = false;
    this.resetNewTransaction();
  }

  resetNewTransaction() {
    this.newTransaction = {
      id: 0,
      description: '',
      amount: 0,
      type: 'INCOME',
      category: '',
      date: new Date().toISOString().substring(0, 10),
    };
  }

  addTransaction() {
    console.log('Transaktion wird gesendet:', this.newTransaction);
    this.txService.create(this.newTransaction).subscribe((createdTx) => {
      this.latestTransactions.unshift(createdTx);
      this.updateChartAndTotals();
      this.closeAddModal();
    });
  }

  updateChartAndTotals() {
    const incomePerMonth = Array(12).fill(0);
    const expensePerMonth = Array(12).fill(0);
    this.totalIncome = 0;
    this.totalExpenses = 0;

    this.latestTransactions.forEach((tx) => {
      const month = new Date(tx.date).getMonth();
      const type = tx.type?.toUpperCase();

      if (type === 'INCOME') {
        this.totalIncome += tx.amount;
        incomePerMonth[month] += tx.amount;
      } else if (type === 'EXPENSE') {
        this.totalExpenses += tx.amount;
        expensePerMonth[month] += tx.amount;
      }
    });

    this.balance = this.totalIncome - this.totalExpenses;

    this.chartData.datasets[0].data = incomePerMonth;
    this.chartData.datasets[1].data = expensePerMonth;
  }

  showDeleteModal = false;
  selectedTransaction: Transaction | null = null;

  openDeleteModal(tx: Transaction): void {
    this.selectedTransaction = tx;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.selectedTransaction = null;
    this.showDeleteModal = false;
  }

  confirmDelete(): void {
    if (this.selectedTransaction?.id != null) {
      this.txService.delete(this.selectedTransaction.id).subscribe(() => {
        this.loadTransactions(); // Daten neu laden
        this.closeDeleteModal();
      });
    }
  }

  loadTransactions(): void {
    this.txService.getAll().subscribe((transactions: Transaction[]) => {
      const incomePerMonth = Array(12).fill(0);
      const expensePerMonth = Array(12).fill(0);

      this.latestTransactions = transactions.sort(
        (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
      );

      this.totalIncome = 0;
      this.totalExpenses = 0;

      transactions.forEach((tx) => {
        const month = new Date(tx.date).getMonth();
        const type = tx.type?.toUpperCase();

        if (type === 'INCOME') {
          this.totalIncome += tx.amount;
          incomePerMonth[month] += tx.amount;
        } else if (type === 'EXPENSE') {
          this.totalExpenses += tx.amount;
          expensePerMonth[month] += tx.amount;
        }
      });

      this.balance = this.totalIncome - this.totalExpenses;

      this.chartData.datasets[0].data = incomePerMonth;
      this.chartData.datasets[1].data = expensePerMonth;
    });
  }

  showEditModal = false;
  editTransaction: Transaction | null = null;

  openEditModal(tx: Transaction): void {
    this.editTransaction = { ...tx }; // Kopie, um Original nicht direkt zu ändern
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.editTransaction = null;
    this.showEditModal = false;
  }

  saveEditTransaction(): void {
    if (this.editTransaction && this.editTransaction.id != null) {
      this.txService
        .update(this.editTransaction.id, this.editTransaction)
        .subscribe(() => {
          this.loadTransactions();
          this.closeEditModal();
        });
    }
  }
}
