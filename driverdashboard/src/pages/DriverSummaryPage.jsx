import React from "react";

const DriverSummaryPage = () => {
    // Placeholder content (replace with real data later)
    const drivers = [
        { name: "Driver A", deliveries: 52 },
        { name: "Driver B", deliveries: 47 },
        { name: "Driver C", deliveries: 39 },
        { name: "Driver D", deliveries: 33 },
        { name: "Driver E", deliveries: 28 },
        { name: "Driver F", deliveries: 22 },
    ];

    return (
        <div>
            <h1 className="text-2xl font-bold mb-4">All Drivers</h1>
            <table className="w-full text-left bg-white shadow rounded">
                <thead className="bg-gray-100">
                <tr>
                    <th className="p-3">Name</th>
                    <th className="p-3">Deliveries</th>
                </tr>
                </thead>
                <tbody>
                {drivers.map((driver, index) => (
                    <tr key={index} className="border-t">
                        <td className="p-3">{driver.name}</td>
                        <td className="p-3">{driver.deliveries}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default DriverSummaryPage;
