import React from "react";
import { Link } from "react-router-dom";

const AdminDashboard = () => {
    // Example mock data (replace with API calls later)
    const topDrivers = ["Driver A", "Driver B", "Driver C", "Driver D", "Driver E"];
    const topPartners = ["Partner X", "Partner Y", "Partner Z", "Partner M", "Partner N"];
    const lowInventory = ["Pharmacy 1", "Pharmacy 2", "Clinic A", "Partner X", "Clinic C"];

    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-6">Admin Overview</h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {/* Top Drivers */}
                <div className="bg-white p-6 rounded shadow">
                    <h2 className="text-lg font-bold mb-4">Top 5 Drivers</h2>
                    <ul className="text-gray-700 space-y-1">
                        {topDrivers.map((driver, i) => (
                            <li key={i}>🚚 {driver}</li>
                        ))}
                    </ul>
                    <Link
                        to="/admin/drivers"
                        className="text-blue-600 hover:underline mt-4 block"
                    >
                        View All Drivers →
                    </Link>
                </div>

                {/* Top Partners */}
                <div className="bg-white p-6 rounded shadow">
                    <h2 className="text-lg font-bold mb-4">Top 5 Partners</h2>
                    <ul className="text-gray-700 space-y-1">
                        {topPartners.map((partner, i) => (
                            <li key={i}>🏥 {partner}</li>
                        ))}
                    </ul>
                    <Link
                        to="/admin/partners"
                        className="text-blue-600 hover:underline mt-4 block"
                    >
                        View All Partners →
                    </Link>
                </div>

                {/* Low Inventory Alerts */}
                <div className="bg-white p-6 rounded shadow">
                    <h2 className="text-lg font-bold mb-4">Low Inventory Alerts</h2>
                    <ul className="text-gray-700 space-y-1">
                        {lowInventory.map((entry, i) => (
                            <li key={i}>⚠️ {entry}</li>
                        ))}
                    </ul>
                    <Link
                        to="/admin/alerts"
                        className="text-blue-600 hover:underline mt-4 block"
                    >
                        View All Alerts →
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
