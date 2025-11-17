import React from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import Page from "../components/layout/Page";
import { Link } from "react-router-dom";

const Profile: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return (
      <Page>
        <Navbar />
        <div className="min-h-[60vh] flex items-center justify-center px-4">
          <div className="max-w-md w-full bg-white rounded-2xl shadow-md border border-slate-200 p-6 text-center">
            <p className="text-red-500 mb-4">
              You must be logged in to view your profile.
            </p>
            <Link
              to="/login"
              className="inline-block rounded-lg bg-blue-600 text-white px-4 py-2 text-sm font-medium hover:bg-blue-700 transition"
            >
              Go to login
            </Link>
          </div>
        </div>
        <Footer />
      </Page>
    );
  }

  const formattedDate = user.createdAt
    ? new Date(user.createdAt).toLocaleDateString()
    : "Unknown";

  return (
    <Page>
      <Navbar />

      <div className="min-h-[60vh] flex items-center justify-center px-4">
        <div className="w-full max-w-xl bg-white rounded-2xl shadow-md border border-slate-200 p-6">
          <h1 className="text-2xl font-semibold mb-2 text-slate-900">
            Your Profile
          </h1>
          <p className="text-sm text-slate-500 mb-6">
            Manage your account details and navigate to your listings.
          </p>

          <div className="space-y-4">
            <div>
              <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                Name
              </h2>
              <p className="text-lg text-slate-900">{user.name}</p>
            </div>

            <div>
              <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                Email
              </h2>
              <p className="text-lg text-slate-900">{user.email}</p>
            </div>

            <div>
              <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                Member since
              </h2>
              <p className="text-lg text-slate-900">{formattedDate}</p>
            </div>
          </div>

          {/* ACTION BUTTONS */}
          <div className="mt-8 flex flex-col sm:flex-row gap-3">
            <Link
              to="/listings"
              className="flex-1 inline-flex items-center justify-center rounded-lg bg-blue-600 text-white text-sm font-medium py-2.5 hover:bg-blue-700 transition"
            >
              View Listings
            </Link>

            <Link
              to="/"
              className="flex-1 inline-flex items-center justify-center rounded-lg border border-slate-300 text-slate-800 text-sm font-medium py-2.5 hover:bg-slate-50 transition"
            >
              Back to Home
            </Link>
          </div>
        </div>
      </div>

      <Footer />
    </Page>
  );
};

export default Profile;
