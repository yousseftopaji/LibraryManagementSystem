using System.Linq;
using Bunit;
using Microsoft.AspNetCore.Components;
using Xunit;

namespace BlazorApp.Tests.Components;

public class ConfirmationDialogTests
{
    [Fact]
    public void ConfirmButton_InvokesOnConfirm()
    {
        using var ctx = new Bunit.BunitContext();

        bool confirmed = false;
        bool canceled = false;

        var cut = ctx.Render<BlazorApp.Components.Pages.ConfirmationDialog>(parameters => parameters
            .Add(p => p.IsVisible, true)
            .Add(p => p.Title, "Delete Item")
            .Add(p => p.Message, "Do you want to delete this item?")
            .Add(p => p.OnConfirm, EventCallback.Factory.Create(this, () => { confirmed = true; }))
            .Add(p => p.OnCancel, EventCallback.Factory.Create(this, () => { canceled = true; }))
        );

        // Find the confirm button by its text
        var confirmButton = cut.FindAll("button").FirstOrDefault(b => b.TextContent.Contains("Confirm") || b.TextContent.Contains("Confirm"));
        if (confirmButton == null)
            confirmButton = cut.Find("button.btn.btn-primary");
        confirmButton.Click();

        Assert.True(confirmed);
        Assert.False(canceled);
    }

    [Fact]
    public void CancelButton_InvokesOnCancel()
    {
        using var ctx = new Bunit.BunitContext();

        bool confirmed = false;
        bool canceled = false;

        var cut = ctx.Render<BlazorApp.Components.Pages.ConfirmationDialog>(parameters => parameters
            .Add(p => p.IsVisible, true)
            .Add(p => p.Title, "Delete Item")
            .Add(p => p.Message, "Do you want to delete this item?")
            .Add(p => p.OnConfirm, EventCallback.Factory.Create(this, () => { confirmed = true; }))
            .Add(p => p.OnCancel, EventCallback.Factory.Create(this, () => { canceled = true; }))
        );

        // Find the cancel button by its text or class
        var cancelButton = cut.FindAll("button").FirstOrDefault(b => b.TextContent.Contains("Cancel"));
        if (cancelButton == null)
            cancelButton = cut.Find("button.btn.btn-secondary");
        cancelButton.Click();

        Assert.False(confirmed);
        Assert.True(canceled);
    }
}
