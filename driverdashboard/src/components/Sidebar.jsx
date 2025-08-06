import React from "react";

const Sidebar = () => {
    return (
        <aside className="w-64 bg-gray-100 h-full shadow-md p-4">
            <nav>
                <ul className="space-y-4 font-semibold">
                    <li><a href="#">Dashboard</a></li>
                    <li><a href="#">Drivers</a></li>
                    <li><a href="#">Analytics</a></li>
                    <li><a href="#">Partners</a></li>
                    <li><a href="#">Alerts</a></li>
                </ul>
            </nav>
        </aside>
    );
};

export default Sidebar;
