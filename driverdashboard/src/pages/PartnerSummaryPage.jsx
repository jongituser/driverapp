import React from "react";

const PartnerSummaryPage = () => {
    const partners = [
        { name: "Partner X", orders: 130 },
        { name: "Partner Y", orders: 120 },
        { name: "Partner Z", orders: 112 },
        { name: "Clinic A", orders: 105 },
        { name: "Pharmacy B", orders: 95 },
        { name: "Pharmacy C", orders: 89 },
    ];

    return (
        <div>
            <h1 className="text-2xl font-bold mb-4">All Partners</h1>
            <table className="w-full text-left bg-white shadow rounded">
                <thead className="bg-gray-100">
                <tr>
                    <th className="p-3">Name</th>
                    <th className="p-3">Total Orders</th>
                </tr>
                </thead>
                <tbody>
                {partners.map((partner, index) => (
                    <tr key={index} className="border-t">
                        <td className="p-3">{partner.name}</td>
                        <td className="p-3">{partner.orders}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default PartnerSummaryPage;
