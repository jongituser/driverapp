import React from "react";

const Navbar = () => {
    return (
        <header className="bg-blue-700 text-white px-6 py-4 flex justify-between items-center shadow">
            <h1 className="text-xl font-bold">Admin Dashboard</h1>
            <button className="bg-white text-blue-700 font-semibold px-4 py-2 rounded hover:bg-gray-100">
                Logout
            </button>
        </header>
    );
};

export default Navbar;
