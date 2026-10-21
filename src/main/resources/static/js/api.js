// Service Module: Handles REST API calls
const BankAPI = {
    async getAccount(accNo) {
        const res = await fetch(`/api/accounts/${accNo}`);
        if (!res.ok) throw new Error("Account not found");
        return await res.json();
    },

    async transferFunds(payload) {
        const res = await fetch("/api/transfers", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        return await res.json();
    }
};