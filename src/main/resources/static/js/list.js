(function() {

  function $(sel, ctx=document) { return ctx.querySelector(sel); }
  function $all(sel, ctx=document) { return Array.from(ctx.querySelectorAll(sel)); }

  const toast = $("#toast");

  function showToast(msg, type="ok") {
    if (!toast) return;
    toast.textContent = msg;
    toast.className = "toast toast--" + type;
    toast.hidden = false;
    setTimeout(() => {
      toast.classList.add("is-hiding");
      setTimeout(() => { toast.hidden = true; toast.classList.remove("is-hiding"); }, 350);
    }, 3000);
  }

  async function deleteUser(cpf, btn) {
    if (!confirm("Tem certeza que deseja excluir o usuário " + cpf + "?")) return;

    btn.disabled = true;
    btn.textContent = "Excluindo...";

    try {
      const resp = await fetch(`/usuarios/${cpf}`, { method: "DELETE" });

      if (resp.ok) {
        const row = document.querySelector(`tr[data-cpf="${cpf}"]`);
        if (row) {
            row.classList.add("fade-out");
            setTimeout(() => row.remove(), 300);
        }
        showToast("Usuário deletado com sucesso!");
      } else if (resp.status === 404) {
        showToast("Usuário já não existe (404).", "warn");
      } else {
        const txt = await resp.text();
        showToast("Erro ao deletar: " + txt, "error");
      }
    } catch (e) {
      showToast("Falha de rede ao deletar.", "error");
    } finally {
      btn.disabled = false;
      btn.textContent = "Excluir";
    }
  }

  function wire() {
    $all(".js-delete-user").forEach(btn => {
      btn.addEventListener("click", () => {
        const cpf = btn.getAttribute("data-cpf");
        deleteUser(cpf, btn);
      });
    });
  }

  document.addEventListener("DOMContentLoaded", wire);

})();