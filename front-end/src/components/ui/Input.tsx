export const Input = (props: React.InputHTMLAttributes<HTMLInputElement>) => (
  <input {...props}
    className={`w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-700 placeholder-slate-400 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 ${props.className ?? ""}`} />
);