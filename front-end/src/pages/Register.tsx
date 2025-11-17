import React from "react";
import Page from "../components/layout/Page";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import { Section } from "../components/ui/Section";
import RegisterForm from "../components/register/RegisterForm";
import { Link } from "react-router-dom";

const Register: React.FC = () => {
  return (
    <Page>
      <Navbar />

      <Section title="">
        <div className="min-h-[60vh] flex items-center justify-center px-4">
          <div className="w-full max-w-md bg-white rounded-2xl shadow-md border border-slate-200 p-6">
            <h1 className="text-2xl font-semibold mb-2 text-slate-900">
              Create your account
            </h1>
            <p className="text-sm text-slate-500 mb-6">
              Sign up to save listings, manage your profile and more.
            </p>

            <RegisterForm />

            {/* LOGIN CTA */}
            <div className="mt-6 text-center">
              <p className="text-sm text-slate-600 mb-2">
                Already have an account?
              </p>

              <Link
                to="/login"
                className="inline-block w-full rounded-lg bg-slate-100 text-slate-800 text-sm font-medium py-2.5 hover:bg-slate-200 transition"
              >
                Go to login
              </Link>
            </div>
          </div>
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Register;
