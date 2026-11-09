(() => {
  const dataNode = document.getElementById('dashboardData');
  if (!dataNode) return;

  const value = (name) => Number(dataNode.dataset[name] || 0);
  const palette = {
    green: '#0b7a5e', mint: '#9acbbb', blue: '#3e78c5',
    amber: '#dc9b22', red: '#c34c45', grid: '#e5ece8',
    text: '#64736d', dark: '#172720'
  };

  function context(canvas) {
    const ratio = window.devicePixelRatio || 1;
    const box = canvas.getBoundingClientRect();
    canvas.width = Math.max(1, Math.floor(box.width * ratio));
    canvas.height = Math.max(1, Math.floor(box.height * ratio));
    const ctx = canvas.getContext('2d');
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
    return { ctx, width: box.width, height: box.height };
  }

  function empty(ctx, width, height) {
    ctx.fillStyle = palette.text;
    ctx.font = '13px Arial';
    ctx.textAlign = 'center';
    ctx.fillText('Sin datos disponibles', width / 2, height / 2);
  }

  function donut(canvas, values, colors) {
    if (!canvas) return;
    const { ctx, width, height } = context(canvas);
    const total = values.reduce((sum, current) => sum + current, 0);
    if (total <= 0) return empty(ctx, width, height);

    const x = width / 2;
    const y = height / 2;
    const radius = Math.min(width, height) * 0.34;
    let start = -Math.PI / 2;

    values.forEach((current, index) => {
      const end = start + (current / total) * Math.PI * 2;
      ctx.beginPath();
      ctx.arc(x, y, radius, start, end);
      ctx.arc(x, y, radius * 0.58, end, start, true);
      ctx.closePath();
      ctx.fillStyle = colors[index];
      ctx.fill();
      start = end;
    });

    ctx.fillStyle = palette.dark;
    ctx.font = '700 28px Arial';
    ctx.textAlign = 'center';
    ctx.fillText(String(total), x, y + 6);
    ctx.fillStyle = palette.text;
    ctx.font = '12px Arial';
    ctx.fillText('total', x, y + 26);
  }

  function horizontalBars(canvas, labels, values, colors, currency) {
    if (!canvas) return;
    const { ctx, width, height } = context(canvas);
    if (Math.max(...values) <= 0) return empty(ctx, width, height);

    const left = 96;
    const right = 20;
    const top = 38;
    const row = Math.min(82, (height - top - 20) / labels.length);
    const barWidth = Math.max(10, width - left - right);
    const max = Math.max(...values, 1);

    labels.forEach((label, index) => {
      const y = top + index * row;
      ctx.fillStyle = palette.text;
      ctx.font = '12px Arial';
      ctx.textAlign = 'left';
      ctx.fillText(label, 0, y + 17);
      ctx.fillStyle = palette.grid;
      ctx.fillRect(left, y, barWidth, 22);
      ctx.fillStyle = colors[index];
      ctx.fillRect(left, y, barWidth * (values[index] / max), 22);
      ctx.fillStyle = palette.dark;
      ctx.textAlign = 'right';
      const shown = currency
        ? 'S/ ' + values[index].toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
        : String(values[index]);
      ctx.fillText(shown, width - right, y + 17);
    });
  }

  function render() {
    const totalTurnos = value('turnosHoy');
    const programados = Math.min(value('turnosProgramados'), totalTurnos);

    donut(
      document.getElementById('turnosChart'),
      [programados, Math.max(0, totalTurnos - programados)],
      [palette.green, palette.mint]
    );

    horizontalBars(
      document.getElementById('coberturaChart'),
      ['Agricultores', 'Parcelas'],
      [value('agricultores'), value('parcelas')],
      [palette.blue, palette.amber],
      false
    );

    horizontalBars(
      document.getElementById('finanzasChart'),
      ['Ingresos', 'Deuda'],
      [value('ingresos'), value('deuda')],
      [palette.green, palette.red],
      true
    );
  }

  let resizeTimer;
  window.addEventListener('resize', () => {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(render, 160);
  });
  render();
})();
