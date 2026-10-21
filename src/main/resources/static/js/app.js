document.addEventListener("DOMContentLoaded", async function () {
    // 1. Modular Components Loader (Includes navbar & footer)
    await loadComponent("navbar-container", "includes/navbar.html");
    await loadComponent("footer-container", "includes/footer.html");

    const sourceAcc = "501004289123";
    const displayBalance = document.getElementById("displayBalance");
    const transferBtn = document.getElementById("transferBtn");
    const alertBox = document.getElementById("alertBox");
    const txnTableBody = document.getElementById("txnTableBody");

    // 2. Fetch Initial Balance
    loadBalance();

    async function loadBalance() {
        try {
            const data = await BankAPI.getAccount(sourceAcc);
            displayBalance.textContent = `₹${parseFloat(data.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
        } catch (e) {
            console.error("Balance fetch error:", e);
        }
    }

    // 3. Handle Fund Transfer
    transferBtn.addEventListener("click", async function () {
        const destAcc = document.getElementById("destinationAccount").value.trim();
        const amountVal = document.getElementById("amount").value.trim();

        if (!destAcc || !amountVal) {
            showAlert("danger", "Please fill in recipient account and amount.");
            return;
        }

        const amount = parseFloat(amountVal);
        if (isNaN(amount) || amount <= 0) {
            showAlert("danger", "Amount must be strictly greater than ₹0.");
            return;
        }

        transferBtn.disabled = true;
        transferBtn.textContent = "Processing Transfer...";

        try {
            const data = await BankAPI.transferFunds({
                sourceAccountNumber: sourceAcc,
                destinationAccountNumber: destAcc,
                amount: amount
            });

            if (data.status === "SUCCESS") {
                showAlert("success", `Success: ₹${amount} transferred to ${destAcc}! Txn ID: #${data.transactionId}`);
                document.getElementById("destinationAccount").value = "";
                document.getElementById("amount").value = "";
                await loadBalance();
                addTxnRow(data.transactionId, `Transfer to ${destAcc}`, amount, "SUCCESS");
            } else {
                showAlert("danger", `Failed: ${data.message}`);
            }
        } catch (err) {
            showAlert("danger", "Server connection failed. Make sure backend is running.");
        } finally {
            transferBtn.disabled = false;
            transferBtn.textContent = "Pay Securely & Transfer";
        }
    });

    function showAlert(type, msg) {
        alertBox.className = `alert alert-${type} mt-3 mb-0`;
        alertBox.textContent = msg;
        alertBox.classList.remove("d-none");
    }

    function addTxnRow(id, desc, amt, status) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>#TXN${id}</strong></td>
            <td>${desc}<br><small class="text-muted">Debit Transfer</small></td>
            <td class="text-danger fw-bold">-₹${parseFloat(amt).toFixed(2)}</td>
            <td><span class="badge bg-success">${status}</span></td>
        `;
        txnTableBody.prepend(tr);
    }
});

// Helper function to dynamically inject HTML includes
async function loadComponent(targetId, filePath) {
    try {
        const res = await fetch(filePath);
        if (res.ok) {
            document.getElementById(targetId).innerHTML = await res.text();
        }
    } catch (err) {
        console.error(`Error loading ${filePath}:`, err);
    }
}