import React from "react";

const LowInventoryAlertPage = () => {
    const alerts = [
        { partner: "Pharmacy 1", item: "Insulin", stock: 3 },
        { partner: "Clinic A", item: "Antibiotics", stock: 5 },
        { partner: "Partner X", item: "Vitamins", stock: 2 },
        { partner: "Pharmacy 2", item: "Painkillers", stock: 4 },
    ];

    return (
        <div>
            <h1 className="text-2xl font-bold mb-4">Low Inventory Alerts</h1>
            <table className="w-full text-left bg-white shadow rounded">
                <thead className="bg-gray-100">
                <tr>
                    <th className="p-3">Partner</th>
                    <th className="p-3">Item</th>
                    <th className="p-3">Stock Left</th>
                </tr>
                </thead>
                <tbody>
                {alerts.map((alert, index) => (
                    <tr key={index} className="border-t">
                        <td className="p-3">{alert.partner}</td>
                        <td className="p-3">{alert.item}</td>
                        <td className="p-3">{alert.stock}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default LowInventoryAlertPage;
